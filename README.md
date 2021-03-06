[![Build Status](https://travis-ci.com/ONSdigital/census-fwmt-outcome-service.svg?branch=master)](https://travis-ci.com/ONSdigital/census-fwmt-outcome-service) [![codecov](https://codecov.io/gh/ONSdigital/census-fwmt-outcome-service/branch/master/graph/badge.svg)](https://codecov.io/gh/ONSdigital/census-fwmt-outcome-service) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/1bad894364ed49f29a41193cf9e1e8ff)](https://www.codacy.com/app/ONSDigital_FWMT/census-fwmt-outcome-service?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ONSdigital/census-fwmt-outcome-service&amp;utm_campaign=Badge_Grade)

# census-fwmt-outcome-service
This service is a gateway between Total Mobile's COMET interface and FWMT outcome service.

It receives a JSON response from TM, transforms it into a FWMT Canonical and places the message onto the Gateway.Outcome RabbitMQ Queue

![](/outcomeservice-highlevel.png "outcomeservice highlevel diagram")

## Quick Start

Requires RabbitMQ to start:

	docker run --name rabbit -p 5671-5672:5671:5672 -p 15671-15672:15671-15672 -d rabbitmq:3.6-management

To run: 

    ./gradlew bootRun

## tm-outcome

![](tm-outcome.png "tm - census - outcome - mapping")

## Copyright
Copyright (C) 2018 Crown Copyright (Office for National Statistics)
