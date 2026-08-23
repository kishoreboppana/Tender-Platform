# Install prepare-commit-msg hook (strips Cursor co-author from commits)
$repoRoot = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
if (-not (Test-Path "$repoRoot\.git")) {
    $repoRoot = Split-Path $PSScriptRoot -Parent
}
$hooksDir = Join-Path $repoRoot ".git\hooks"
$src = Join-Path $PSScriptRoot "git-hooks\prepare-commit-msg"
$dest = Join-Path $hooksDir "prepare-commit-msg"
Copy-Item -Force $src $dest
Write-Host "Installed: $dest"
