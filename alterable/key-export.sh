#!/bin/bash
export SSH_PRV_KEY="$(cat ~/.ssh/id_rsa)" \
&& docker-compose -f autossh.docker-compose.yaml up