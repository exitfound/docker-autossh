#!/bin/bash
autossh -N -M 0 -o "ServerAliveInterval 45" -o "ServerAliveCountMax 2" -o "StrictHostKeyChecking no" \
    -i /home/autossh/.ssh/id_rsa \
    -p $SSH_TUNNEL_PORT \
    $SSH_TUNNEL_MODE \
    $SSH_TUNNEL_LOCALPORT:$SSH_TUNNEL_IP:$SSH_TUNNEL_REMOTEPORT \
    $SSH_TUNNEL_USER@$SSH_TUNNEL_HOST