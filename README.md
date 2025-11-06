# Project Overview

I will automate tests and perform quality checks using GitHub Actions.


* BarnesAndNoble: A book purchasing system that handles order processing, inventory checking, and price calculation
* Amazon: A shopping cart system with flexible pricing rules including delivery costs and electronics surcharges


## Part 1 – Project Setup and Testing Practice

* Specification-Based Testing: Tests based on requirements including boundary conditions (null inputs, empty orders, insufficient quantities) and normal operations (single/multiple book purchases)
* Structural-Based Testing: Tests covering branches, loops, and execution paths including edge cases like zero quantity and exact quantity matches

## Part 2 – Automate Testing with GitHub Actions
The CI pipeline automatically runs on every push to the main branch.

* Static Analysis: Checkstyle validation during the validate phase
* Code Coverage: JaCoCo reports generated

[![.github/workflows/SE333_CI.yml](https://github.com/kdang6/Assignment-5/actions/workflows/SE333_CI.yml/badge.svg)](https://github.com/kdang6/Assignment-5/actions/workflows/SE333_CI.yml)
