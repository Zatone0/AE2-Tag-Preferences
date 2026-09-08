"""Prepare release assets or upload to CurseForge. Standard library only."""
import hashlib
import json
import os
from pathlib import Path
import re
import sys
import urllib.request
import uuid
import zipfile

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "build/release"

def version():
    value = re.search(r"^mod_version=(.+)$", (ROOT / "gradle.properties").read_text(), re.M).group(1).strip()
    if not re.fullmatch(r"[0-9]+\.[0-9]+\.[0-9]+", value):
        raise ValueError("Full releases require a numeric X.Y.Z version")
    return value

def prepare():
    v = version()
    ref = os.environ.get("GITHUB_REF", "")
    if ref.startswith("refs/tags/") and ref.removeprefix("refs/tags/").removeprefix("v") != v:
        raise ValueError("Tag does not match mod_version")
    text = (ROOT / "CHANGELOG.md").read_text(encoding="utf-8")
    match = re.search(r"^# (?:AE2 Tag Preferences )?" + re.escape(v) + r"\s*\n(.*?)(?=^# |\Z)", text, re.M | re.S)
    if not match or not match.group(1).strip():
        raise ValueError("Missing changelog entry for release version")
    return f"# AE2 Tag Preferences {v}\n\n" + match.group(1).strip() + "\n"

def package():
    notes = prepare()
    v = version()
    OUT.mkdir(parents=True, exist_ok=True)
    sums = []
    for suffix in ("", "-sources"):
        name = f"ae2-tag-preferences-1.20.1-{v}{suffix}.jar"
        data = (ROOT / "build/libs" / name).read_bytes()
        with zipfile.ZipFile(ROOT / "build/libs" / name) as jar:
            for required in ("LICENSE", "NOTICE.md", "licenses/GPL-3.0.txt"):
                jar.getinfo(required)
            if not suffix:
                metadata = jar.read("META-INF/mods.toml").decode()
                if f'version="{v}"' not in metadata or 'license="LGPL-3.0-only"' not in metadata:
                    raise ValueError("JAR version/license mismatch")
        (OUT / name).write_bytes(data)
        sums.append(hashlib.sha256(data).hexdigest() + "  " + name)
    (OUT / "SHA256SUMS.txt").write_text("\n".join(sums) + "\n")
    (OUT / "notes.md").write_text(notes, encoding="utf-8")

def curseforge():
    token = os.environ.get("CURSEFORGE_TOKEN", "")
    project = os.environ.get("CURSEFORGE_PROJECT_ID", "")
    if not token or not project.isdigit():
        raise ValueError("Set CURSEFORGE_TOKEN secret and numeric CURSEFORGE_PROJECT_ID variable")
    v = version()
    name = f"ae2-tag-preferences-1.20.1-{v}.jar"
    data = (OUT / name).read_bytes()
    expected = dict(line.split("  ", 1)[::-1] for line in (OUT / "SHA256SUMS.txt").read_text().splitlines())
    if hashlib.sha256(data).hexdigest() != expected[name]:
        raise ValueError("Upload checksum mismatch")
    metadata = {
        "changelog": (OUT / "notes.md").read_text(encoding="utf-8"),
        "changelogType": "markdown",
        "displayName": f"AE2 Tag Preferences {v} - Forge 1.20.1",
        "gameVersionNames": ["1.20.1", "Forge", "Java 17", "Client"],
        "releaseType": "release",
        "isMarkedForManualRelease": False,
        "relations": {"projects": [
            {"slug": "applied-energistics-2", "type": "requiredDependency"},
            {"slug": "ex-pattern-provider", "type": "optionalDependency"}
        ]}
    }
    boundary = "release-" + uuid.uuid4().hex
    body = (f'--{boundary}\r\nContent-Disposition: form-data; name="metadata"\r\n\r\n'.encode()
            + json.dumps(metadata).encode()
            + f'\r\n--{boundary}\r\nContent-Disposition: form-data; name="file"; filename="{name}"\r\nContent-Type: application/java-archive\r\n\r\n'.encode()
            + data + f"\r\n--{boundary}--\r\n".encode())
    request = urllib.request.Request(f"https://minecraft.curseforge.com/api/projects/{project}/upload-file", data=body,
        headers={"X-Api-Token": token, "Content-Type": "multipart/form-data; boundary=" + boundary})
    # Do not retry uncertain POST results: check the project first to avoid duplicates.
    with urllib.request.urlopen(request, timeout=120) as response:
        result = json.load(response)
    if not isinstance(result.get("id"), int):
        raise ValueError("Upload response did not contain a file ID; inspect CurseForge before retrying")
    print("Uploaded CurseForge file", result["id"])

if __name__ == "__main__":
    {"prepare": prepare, "package": package, "curseforge": curseforge}[sys.argv[1]]()
