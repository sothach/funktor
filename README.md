# Funktor
Play / Spark sandbox

### Status
[![Build Status](https://travis-ci.org/sothach/funktor.png)](https://travis-ci.org/sothach/funktor)
[![Coverage Status](https://coveralls.io/repos/github/sothach/funktor/badge.svg?branch=master)](https://coveralls.io/github/sothach/funktor?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/055bcd5660f941df8f9d8926e6672f62)](https://www.codacy.com/project/sothach/funktor/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=sothach/funktor&amp;utm_campaign=Badge_Grade_Dashboard)

## Overview
This app is intended as a sandbox to experiment with the various aspects of Apache Spark and it's libraries.
A web interface is provided to render the results of data manipulations in a convenient manner.
The target data source is a Postgres database, preloaded with sample datasets


## Testing
### Running the tests
Run the test suite to verify correct behaviour.  

From the command line:
```sbtshell
% sbt test
```
### Test Coverage Report
To measure test coverage, this app uses the 'scoverage' SBT plugin.
To create the report, from the command line:
```sbtshell
% sbt coverage test coverageReport
```

## Author
* [Roy Phillips](mailto:phillips.roy@gmail.com)

## License
[![License](https://licensebuttons.net/l/by/3.0/88x31.png)](https://creativecommons.org/licenses/by/4.0/) 

(c) 2018 This project is licensed under Creative Commons License

[Attribution 4.0 International (CC BY 4.0)](LICENSE.md)

