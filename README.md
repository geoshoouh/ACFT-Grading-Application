# ACFT-Grading-Application
Eliminates the need for paper scorecards and manual score consolidation for the Army Combat Fitness Test

Features:
-On-site decentralized digitization of ACFT score data
-Export testing group data to CSV
-Password authentication for test group access
-Automatic conversion of raw score data to scaled scores
-Distinct admin privileges per testing group
    *Test group deletion
    *Lock editing for a test group
    *Export data
-Test group data is deleted after exportation or 24 hours.

Project specs:
-Backend written in Java using Spring Boot
-Dependencies: Spring Data JPA, Thymeleaf, Spring Web, H2 Database, Spring Boot Dev Tools, GSON
-Front-end exclusively HTML, CSS, JS

Development progress:
-Stack fully integrated
-Can persist test groups and soldiers via the UI
-Can retrieve test groups and associated soldiers via the UI


