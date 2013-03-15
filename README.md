sparql-bed
==========

sparql against simple bed files without loading them into a triple store.

git clone https://github.com/JervenBolleman/sparql-bed
cd sparql-bed
mvn assembly:assembly
./sparql-bed.sh src/test/resources/example.bed "SELECT ?s WHERE {?s <http://biohackathon.org/resource/faldo#position> ?p}"

For the largest perfomance increase we need to add a join operator that knows when all elements are on single line and avoids loops in that form.

The second jump would be to use tabix for filtering the files instead of looping over all elements.
