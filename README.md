# Violation Comments Lib [![Build Status](https://travis-ci.org/tomasbjerre/violation-comments-lib.svg?branch=master)](https://travis-ci.org/tomasbjerre/violation-comments-lib) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/se.bjurr.violations/violation-comments-lib/badge.svg)](https://maven-badges.herokuapp.com/maven-central/se.bjurr.violations/violation-comments-lib)

This is a library that helps working with comments from static code analysis.

It supports the same formats as [Violations Lib](https://github.com/tomasbjerre/violations-lib).

It is used by these libraries:
 * [Violation Comments to GitHub Lib](https://github.com/tomasbjerre/violation-comments-to-github-lib).

## Developer instructions

To build the code, have a look at `.travis.yml`.

To do a release you need to do `./gradlew release` and release the artifact from [staging](https://oss.sonatype.org/#stagingRepositories). More information [here](http://central.sonatype.org/pages/releasing-the-deployment.html).
