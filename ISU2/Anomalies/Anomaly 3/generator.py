S = ' '   # space
T = '\t'  # tab
N = '\n'  # newline

def push(n):
    result = S + S
    result += S
    if n == 0:
        result += S
    else:
        binary = bin(n)[2:]
        for bit in binary:
            result += S if bit == '0' else T
    result += N
    return result

def print_char():
    return T + N + S + S

def end():
    return N + N + N

sentence = "cdc{whitespace_matters}\nCongrats! Here's how to encode anything using whitespaces: https://pastebin.com/x7gtZvZa"

program = ""
for char in sentence:
    program += push(ord(char))
    program += print_char()
program += end()

with open("challenge.ws", "w") as f:
    f.write(program)

print(f"Generated {len(program)} bytes")