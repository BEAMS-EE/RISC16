### Command-line generation of the jar, example for the sequential simulator, from the ```bin/``` directory:

```
jar -c -f ../jar/RiSC16_pipelinev1.1.jar -m MANIFEST.MF risc16_pipeline/*
```

with the manifest simply pointing to the entry point:

```
Manifest-Version: 1.0
Main-Class: risc16_pipeline.Principal
```

- The movi pseudo-instruction is replaced with the combination of lui and addi. The simulator currently requires the user to add a nop instruction after the movi, so that during the assembly step, the couple movi/nop is replaced with lui/addi.
An improvement could be to remove the need for that nop. When the assembler detects a movi, he should shift all the line in the code to insert the lui/addi.
