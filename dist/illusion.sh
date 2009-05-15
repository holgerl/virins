#!/bin/bash
java -cp Virins.jar:lib/* -Djava.library.path=lib -Dbluecove.jsr82.psm_minimum_off=true eit.headtracking.opengl.Illusion
