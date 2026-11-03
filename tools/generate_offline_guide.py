"""Render the complete User Guide with embedded images (requires Markdown 3.7)."""

import base64
import hashlib
import mimetypes
from pathlib import Path
import re

import markdown

ROOT = Path(__file__).resolve().parents[1]
DOCS = ROOT / "docs"
source = (DOCS / "UserGuide.md").read_text(encoding="utf-8")
body = re.sub(r"\A---\n.*?\n---\n", "", source, flags=re.S)
body = body.replace("* Table of Contents\n{:toc}", "[TOC]")
inputs = {"UserGuide.md"}


def embed_image(match):
    """Embed a local screenshot so the guide has no network dependencies."""
    alt, relative = match.groups()
    path = DOCS / relative
    inputs.add(relative)
    mime = mimetypes.guess_type(path)[0]
    encoded = base64.b64encode(path.read_bytes()).decode("ascii")
    return f"![{alt}](data:{mime};base64,{encoded})"


body = re.sub(r"!\[([^\]]*)\]\(([^)]+)\)", embed_image, body)
emoji_shortcodes = {
    ":information_source:": "ℹ️",
    ":bulb:": "💡",
    ":exclamation:": "❗",
}
for shortcode, emoji in emoji_shortcodes.items():
    body = body.replace(shortcode, emoji)

rendered = markdown.markdown(
    body,
    extensions=["tables", "fenced_code", "toc", "sane_lists", "md_in_html"],
)
page = """<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>InsureConnect User Guide</title>
<style>
body { max-width: 960px; margin: 40px auto; padding: 0 24px; color: #202830;
       font: 17px/1.6 system-ui, sans-serif; overflow-wrap: anywhere; }
h1, h2, h3 { line-height: 1.25; }
h2 { margin-top: 2em; border-bottom: 1px solid #ccd5dd; padding-bottom: .3em; }
a { color: #165ca0; }
img { max-width: 100%; height: auto; }
table { width: 100%; border-collapse: collapse; }
th:first-child, td:first-child { min-width: 10em; width: 20%; }
th, td { text-align: left; border: 1px solid #ccd5dd; padding: 8px; }
code { background: #edf1f5; padding: 2px 4px; }
pre { overflow-x: auto; padding: 12px; background: #edf1f5; }
.notice { background: #edf1f5; padding: 12px 16px; }
</style>
</head>
<body>
<h1>InsureConnect User Guide</h1>
<p class="notice">This complete guide is bundled with InsureConnect and works offline.
External links, such as downloads and Java installation instructions, require internet access.</p>
""" + rendered + "\n</body>\n</html>\n"
output = DOCS / "offline"
output.mkdir(exist_ok=True)
(output / "UserGuide.html").write_bytes(page.encode("utf-8"))
inputs.add("offline/UserGuide.html")
manifest = "".join(f"{hashlib.sha256((DOCS / name).read_bytes()).hexdigest()}  {name}\n"
                   for name in sorted(inputs))
(output / "sources.sha256").write_bytes(manifest.encode("utf-8"))
print("Generated docs/offline/UserGuide.html with embedded screenshots.")
