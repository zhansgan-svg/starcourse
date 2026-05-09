#!/usr/bin/env python3
"""
Dry-run validation system for prompt regression testing.

Three-stage pipeline:
  1. parse     — load prompt template + resolve {{include}} fragments
  2. candidate — substitute {{variables}} from fixture input
  3. script    — validate expected output schema (no LLM call)

Usage: python3 dry-runs/render.py
"""

import json
import os
import re
import sys
from pathlib import Path

# Fix Windows console encoding for Chinese output
if sys.platform == "win32":
    sys.stdout.reconfigure(encoding="utf-8")
    sys.stderr.reconfigure(encoding="utf-8")

# ── paths ──────────────────────────────────────────────────────────────
ROOT = Path(__file__).resolve().parent.parent
PROMPT_DIR = ROOT / "src" / "main" / "resources" / "prompt"
SHARED_DIR = PROMPT_DIR / "_shared"
FIXTURES_DIR = Path(__file__).resolve().parent / "fixtures"

# ── regex ──────────────────────────────────────────────────────────────
RE_INCLUDE = re.compile(r'\{\{include\s+"([^"]+)"\}\}')
RE_VARIABLE = re.compile(r"\{\{(\w+)\}\}")


# ── stage 1: parse ─────────────────────────────────────────────────────
def load_template(prompt_type: str) -> str:
    """Load prompt template and resolve all {{include}} directives."""
    template_path = PROMPT_DIR / f"{prompt_type}.md"
    if not template_path.exists():
        raise FileNotFoundError(f"Template not found: {template_path}")

    raw = template_path.read_text(encoding="utf-8")

    def _resolve_includes(text: str, depth: int = 0) -> str:
        if depth > 5:
            raise RecursionError("{{include}} nesting too deep (>5)")

        def replacer(match: re.Match) -> str:
            fragment_name = match.group(1)
            fragment_path = SHARED_DIR / f"{fragment_name}.txt"
            if not fragment_path.exists():
                raise FileNotFoundError(f"Shared fragment not found: {fragment_path}")
            fragment = fragment_path.read_text(encoding="utf-8")
            # Recursively resolve nested includes
            return _resolve_includes(fragment, depth + 1)

        return RE_INCLUDE.sub(replacer, text)

    return _resolve_includes(raw)


# ── stage 2: candidate ─────────────────────────────────────────────────
def substitute_variables(template: str, variables: dict[str, str]) -> str:
    """Replace {{variable}} placeholders with values from fixture input.

    Returns the rendered prompt. Raises if any placeholder remains unresolved.
    """
    missing = []
    result = template

    def replacer(match: re.Match) -> str:
        key = match.group(1)
        if key in variables:
            return variables[key]
        missing.append(key)
        return match.group(0)  # leave unresolved for error reporting

    result = RE_VARIABLE.sub(replacer, result)

    if missing:
        raise ValueError(f"Unresolved variables: {', '.join(sorted(set(missing)))}")

    return result


# ── stage 3: script (output validation) ────────────────────────────────
def validate_output_schema(prompt_type: str, expected: dict | list | None) -> list[str]:
    """Validate that the expected output definition is well-formed.

    Returns a list of error messages (empty = valid).
    """
    errors: list[str] = []

    if expected is None:
        errors.append("expectedOutput is null or missing")
        return errors

    if prompt_type == "questionnaire":
        if not isinstance(expected, dict):
            errors.append("questionnaire expectedOutput must be an object")
            return errors
        for field in ("nickname", "avatarSuggestion", "positioning"):
            if field not in expected:
                errors.append(f"questionnaire missing required field: {field}")

    elif prompt_type == "script-generation":
        if not isinstance(expected, dict):
            errors.append("script-generation expectedOutput must be an object")
            return errors
        if expected.get("type") != "array":
            errors.append("script-generation expectedOutput.type must be 'array'")
        item_schema = expected.get("itemSchema", {})
        for field in ("title", "hook", "fullScript", "shootingTips"):
            if field not in item_schema:
                errors.append(f"script-generation itemSchema missing field: {field}")

    elif prompt_type == "rewrite":
        if not isinstance(expected, dict):
            errors.append("rewrite expectedOutput must be an object")
            return errors
        if "rewrittenContent" not in expected:
            errors.append("rewrite missing required field: rewrittenContent")

    else:
        errors.append(f"Unknown prompt type: {prompt_type}")

    return errors


def validate_rendered_prompt(rendered: str) -> list[str]:
    """Check that the rendered prompt has no leftover {{placeholders}}."""
    errors: list[str] = []
    leftover = RE_VARIABLE.findall(rendered)
    if leftover:
        errors.append(f"Unresolved placeholders after substitution: {leftover}")
    return errors


# ── runner ──────────────────────────────────────────────────────────────
def run_fixture(fixture_path: Path) -> tuple[str, list[str]]:
    """Run a single fixture through the three-stage pipeline.

    Returns (description, list_of_errors). Empty list = PASS.
    """
    errors: list[str] = []

    # -- load fixture --
    try:
        fixture = json.loads(fixture_path.read_text(encoding="utf-8"))
    except (json.JSONDecodeError, OSError) as e:
        return str(fixture_path), [f"Failed to load fixture: {e}"]

    prompt_type = fixture.get("promptType", "")
    description = fixture.get("description", fixture_path.name)
    variables = fixture.get("input", {})
    expected = fixture.get("expectedOutput")

    # -- stage 1: parse --
    try:
        template = load_template(prompt_type)
    except (FileNotFoundError, RecursionError) as e:
        return description, [f"[parse] {e}"]

    # -- stage 2: candidate --
    try:
        rendered = substitute_variables(template, variables)
    except ValueError as e:
        return description, [f"[candidate] {e}"]

    render_errors = validate_rendered_prompt(rendered)
    errors.extend(f"[candidate] {e}" for e in render_errors)

    # -- stage 3: script --
    schema_errors = validate_output_schema(prompt_type, expected)
    errors.extend(f"[script] {e}" for e in schema_errors)

    return description, errors


def main() -> int:
    if not FIXTURES_DIR.exists():
        print(f"ERROR: Fixtures directory not found: {FIXTURES_DIR}")
        return 1

    fixtures = sorted(FIXTURES_DIR.glob("*.json"))
    if not fixtures:
        print("WARNING: No fixture files found in", FIXTURES_DIR)
        return 0

    passed = 0
    failed = 0

    print("=" * 60)
    print("  StarCourse Prompt Dry-Run Validation")
    print("=" * 60)
    print()

    for fixture_path in fixtures:
        description, errors = run_fixture(fixture_path)
        if errors:
            failed += 1
            print(f"  FAIL  {description}")
            for err in errors:
                print(f"        -> {err}")
        else:
            passed += 1
            print(f"  PASS  {description}")

    print()
    print("-" * 60)
    total = passed + failed
    print(f"  Results: {passed}/{total} passed, {failed}/{total} failed")
    print("-" * 60)

    return 1 if failed > 0 else 0


if __name__ == "__main__":
    sys.exit(main())
