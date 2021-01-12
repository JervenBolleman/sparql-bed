sparql-bed
==========

The idea works, but this is not and won't be production code.
If you are interested in it please let me know for now it is in archive mode.

sparql against simple bed files without loading them into a triple store.

```
git clone https://github.com/JervenBolleman/sparql-bed
cd sparql-bed
mvn assembly:assembly
./sparql-bed.sh src/test/resources/example.bed "SELECT ?s ?p WHERE {?s <http://biohackathon.org/resource/faldo#position> ?p}"
```

See the issue lists for future tasks..
The only requirements are a maven3 and java6+ installation.
