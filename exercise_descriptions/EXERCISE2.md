# Exercise 0

You will find the relevant files at:
 - for Java: `./src/main/java/exercise2/`
 - for Kotlin: sorry, we did not prepare kotlin files for this exercise
   - you are still allowed to implement your solution in Kotlin
   - in this case please implement a class inheriting AbstractBPlusTree
   - you would also have to change the used class in the tests to validate your solution

Relevant files / classes:
 * For your implementation:
   - `src/main/java/exercise2/BPlusTreeJava.java`
   - `src/test/java/exercise2/BPlusTreeJavaTests.java`
 * For experimentation:
   - `src/main/java/exercise2/Ex2Main.java`
 * For information on the B+-Tree nodes:
   - `src/main/java/de/hpi/dbs2/exercise2/*.java`
   - `src/main/kotlin/de/hpi/dbs2/exercise2/*.kt`
   - `src/test/java/de/hpi/dbs2/exercise2/*.java`

## Your tasks

1. Implement the empty method `insert(key, value)` in BPlusTreeJava.
2. Test your implementation by running `gradle test --tests "exercise2.BPlusTreeJavaTests"` or using your IDE's test runner.
3. Pack your exercise with `gradle packExercise2` and upload the resulting .zip file to Moodle.
