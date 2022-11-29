# Exercise 0

You will find the relevant files at:
 - for Java: `./src/main/java/exercise2/`
 - for Kotlin: `./src/main/kotlin/exercise2/`

Relevant files / classes:
 * For your implementation:
   - `src/main/java/exercise2/BPlusTreeJava.java` / `src/main/kotlin/exercise2/BPlusTreeKotlin.kt`
   - `src/test/java/exercise2/BPlusTreeJavaTests.java`
 * For experimentation:
   - `src/main/java/exercise2/Ex2Main.java` / `src/main/kotlin/exercise2/Ex2Main.kt`
 * For information on the B+-Tree + nodes:
   - `src/main/java/de/hpi/dbs2/exercise2/*.java`
   - `src/main/kotlin/de/hpi/dbs2/exercise2/*.kt`
   - `src/test/java/de/hpi/dbs2/exercise2/*.java`

## Your tasks

1. Implement the empty method `insert(key, value)` in BPlusTreeJava / BPlusTreeKotlin.
2. Test your implementation by running `gradle test --tests "exercise2.BPlusTreeImplementationTests"` or using your IDE's test runner.
3. Pack your exercise with `gradle packExercise2` and upload the resulting .zip file to Moodle.

## Notes

- It might help to write tests for certain states of the nodes to validate 
  that your algorithm does what you want it to do.
  Check out our existing tests, to get an idea on how to write tests.
- Testing your algorithm by drawing it on paper is also helpful - also "rubber ducking"!
- If you get stuck or are unsure how to tackle an upcoming problem, do message us in Moodle!
