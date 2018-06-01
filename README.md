### Command-line generation of the jar, from the main directory of any simulator, e.g. RiSC16_asm :

Build: (you need *apache ant* to build)
```
cd RiSC16_asm
ant
```

Launch:
```
java -jar *generated-file*.jar
```

- The movi pseudo-instruction is replaced with the combination of lui and addi. The simulator currently requires the user to add a nop instruction after the movi, so that during the assembly step, the couple movi/nop is replaced with lui/addi.
An improvement could be to remove the need for that nop. When the assembler detects a movi, he should shift all the line in the code to insert the lui/addi.
