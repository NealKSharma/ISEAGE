# ClipboardTyper

A lightweight PowerShell script that simulates keyboard input using your clipboard contents.

Useful for **Proxmox, noVNC, VMs, and remote consoles** where `Ctrl+V` doesn't work.

Press **Ctrl+Q** to automatically type your clipboard contents into the active window.

## Requirements

- Windows 10/11
- Windows PowerShell 5.1
- No additional software required

## Installation

### 1. Save the Script

Create a Scripts folder:

```powershell
mkdir "$HOME\Scripts"
```

Save `ClipboardTyper.ps1` inside:

```text
C:\Users\YOUR_USERNAME\Scripts\ClipboardTyper.ps1
```

### 2. Start ClipboardTyper

Run the following command in PowerShell:

```powershell
Start-Process powershell.exe -WindowStyle Hidden -ArgumentList "-NoProfile -STA -File `"$HOME\Scripts\ClipboardTyper.ps1`""
```

ClipboardTyper will now run silently in the background.

## Usage

1. Copy text using `Ctrl+C`.
2. Open your Proxmox console, VM, or terminal.
3. Click where you want the text entered.
4. Press **Ctrl+Q**.

Your clipboard contents will be typed automatically.

## Start Automatically with Windows

Run the following in PowerShell to configure ClipboardTyper to start automatically when you sign in:

```powershell
$script = "$HOME\Scripts\ClipboardTyper.ps1"

$startup = [Environment]::GetFolderPath("Startup")

$shortcutPath = Join-Path $startup "ClipboardTyper.lnk"

$shell = New-Object -ComObject WScript.Shell

$shortcut = $shell.CreateShortcut($shortcutPath)

$shortcut.TargetPath = "$env:SystemRoot\System32\WindowsPowerShell\v1.0\powershell.exe"

$shortcut.Arguments = "-NoProfile -STA -WindowStyle Hidden -File `"$script`""

$shortcut.WorkingDirectory = Split-Path $script

$shortcut.Save()

Write-Host "Startup configured successfully!"
```

This creates a shortcut in your Windows Startup folder.

ClipboardTyper will now automatically launch in the background whenever you sign in.

## Restart ClipboardTyper

After making changes to `ClipboardTyper.ps1`, run:

```powershell
# Stop previous ClipboardTyper instances
Get-CimInstance Win32_Process -Filter "Name = 'powershell.exe'" |
Where-Object {
    $_.ProcessId -ne $PID -and
    $_.CommandLine -like '*ClipboardTyper.ps1*'
} |
ForEach-Object {
    Stop-Process -Id $_.ProcessId -Force
}

# Start updated version
Start-Process powershell.exe `
    -WindowStyle Hidden `
    -ArgumentList "-NoProfile -STA -File `"$HOME\Scripts\ClipboardTyper.ps1`""
```

This stops the previous instance and launches the updated script.

## How It Works

ClipboardTyper registers **Ctrl+Q** as a global Windows hotkey.

When pressed, it:

1. Reads your clipboard contents.
2. Waits for Ctrl and Q to be released.
3. Converts text into simulated keyboard input.
4. Types the contents into the active window.

The script uses Windows Forms and `SendKeys.SendWait()` instead of normal clipboard pasting.

It runs in the background without continuously polling for keyboard input.