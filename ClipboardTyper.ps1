# ClipboardTyper - Background Clipboard Keyboard Simulator
# Ctrl+Q = Type clipboard into active window

$ErrorActionPreference = "Stop"

# Windows Forms requires an STA thread.
if ([Threading.Thread]::CurrentThread.GetApartmentState() -ne "STA") {
    throw "Run with: powershell.exe -STA -File ClipboardTyper.ps1"
}

# Prevent duplicate instances
$createdNew = $false
$mutex = [System.Threading.Mutex]::new(
    $true,
    "Local\ClipboardTyperHotkey_v3",
    [ref]$createdNew
)

if (-not $createdNew) {
    $mutex.Dispose()
    exit
}

try {
    Add-Type -AssemblyName System.Windows.Forms

    Add-Type -ReferencedAssemblies System.Windows.Forms -ErrorAction Stop @"
using System;
using System.Text;
using System.Threading;
using System.Windows.Forms;
using System.Runtime.InteropServices;

public sealed class ClipboardTyperListener : NativeWindow
{
    [DllImport("user32.dll", SetLastError = true)]
    private static extern bool RegisterHotKey(
        IntPtr hWnd, int id, uint modifiers, uint key);

    [DllImport("user32.dll")]
    private static extern bool UnregisterHotKey(IntPtr hWnd, int id);

    [DllImport("user32.dll")]
    private static extern short GetAsyncKeyState(int key);

    private const int WM_HOTKEY = 0x0312;
    private const uint MOD_CONTROL = 0x0002;
    private const uint MOD_NOREPEAT = 0x4000;
    private const uint VK_Q = 0x51;

    private const int VK_CONTROL = 0x11;
    private const int VK_LCONTROL = 0xA2;
    private const int VK_RCONTROL = 0xA3;

    private bool typing = false;

    private static bool IsKeyDown(int key)
    {
        return (GetAsyncKeyState(key) & 0x8000) != 0;
    }

    public void Run()
    {
        CreateHandle(new CreateParams());

        bool registered = false;

        try
        {
            registered = RegisterHotKey(
                Handle,
                1,
                MOD_CONTROL | MOD_NOREPEAT,
                VK_Q
            );

            if (!registered)
            {
                throw new Exception(
                    "Could not register Ctrl+Q. Another program may be using it."
                );
            }

            // Event-driven message loop.
            // No continuous polling while idle.
            Application.Run(new ApplicationContext());
        }
        finally
        {
            if (registered)
                UnregisterHotKey(Handle, 1);

            DestroyHandle();
        }
    }

    protected override void WndProc(ref Message m)
    {
        if (m.Msg == WM_HOTKEY)
        {
            if (m.WParam.ToInt32() == 1 && !typing)
            {
                typing = true;

                try
                {
                    TypeClipboard();
                }
                catch (Exception ex)
                {
                    // Log errors instead of crashing the listener.
                    System.Diagnostics.Debug.WriteLine(ex);
                }
                finally
                {
                    typing = false;
                }
            }

            return;
        }

        base.WndProc(ref m);
    }

    private static void TypeClipboard()
    {
        if (!Clipboard.ContainsText())
            return;

        string text = Clipboard.GetText();

        if (string.IsNullOrEmpty(text))
            return;

        // Wait for BOTH Ctrl and Q to be released.
        // This prevents modifier keys affecting typed text.
        while (
            IsKeyDown(VK_CONTROL) ||
            IsKeyDown(VK_LCONTROL) ||
            IsKeyDown(VK_RCONTROL) ||
            IsKeyDown((int)VK_Q)
        )
        {
            Thread.Sleep(1);
        }

        // Allow Windows to finish processing key releases.
        Thread.Sleep(50);

        StringBuilder keys = new StringBuilder(text.Length);

        foreach (char c in text)
        {
            switch (c)
            {
                case '\r':
                    break;

                case '\n':
                    keys.Append("{ENTER}");
                    break;

                case '\t':
                    keys.Append("{TAB}");
                    break;

                case '+':
                    keys.Append("{+}");
                    break;

                case '^':
                    keys.Append("{^}");
                    break;

                case '%':
                    keys.Append("{%}");
                    break;

                case '~':
                    keys.Append("{~}");
                    break;

                case '(':
                    keys.Append("{(}");
                    break;

                case ')':
                    keys.Append("{)}");
                    break;

                case '{':
                    keys.Append("{{}");
                    break;

                case '}':
                    keys.Append("{}}");
                    break;

                default:
                    keys.Append(c);
                    break;
            }
        }

        // Type clipboard using simulated keyboard events.
        SendKeys.SendWait(keys.ToString());
    }
}
"@

    $listener = [ClipboardTyperListener]::new()
    $listener.Run()
}
finally {
    if ($null -ne $mutex) {
        if ($createdNew) {
            $mutex.ReleaseMutex()
        }

        $mutex.Dispose()
    }
}