## forge-paxexam

# Overview
This plugin uses pax exam and the new karaf container from pax exam. Use the setup command to add necessary dependencies and the new-test command to add a test class.

# Installation
Just run forge console and enter the following command:

`forge git-plugin https://github.com/MarWestermann/forge-paxexam.git`

# Setup
run `exam setup` in a newly created project. This will add the pax exam dependencies to the project. At the moment you can take the default versions of the dependencies.

# Create Test
run `exam new-test --name MyKarafTest` to create a new test class. You will be asked for the package and the karaf version you want to go with. After creating the tests you can directly run `mvn install` or `mvn test` to run the test. Be aware of that the test will fail cause the test method calls the `Assert.fail("not implemented")` method.

