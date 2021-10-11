#!/bin/sh
gunicorn --chdir app main:app -w 5 --threads 2 -b 0.0.0.0:8080
