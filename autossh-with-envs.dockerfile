FROM ubuntu:20.04 AS autossh

ARG SSH_PRV_KEY
ENV SSH_PRV_KEY=${SSH_PRV_KEY}
RUN apt-get update \
    && apt-get install -y \
    autossh \
    net-tools \
    iproute2 \
    && useradd -g users -d /home/autossh autossh \
    && mkdir -p /home/autossh/.ssh/ \
    && chmod -R 700 /home/autossh/.ssh \
    && echo "$SSH_PRV_KEY" > /home/autossh/.ssh/id_rsa \
    && chmod 600 /home/autossh/.ssh/id_rsa \ 
    && chown -R autossh:users /home/autossh

WORKDIR /home/autossh/

USER autossh

ENTRYPOINT autossh -N -M 0 -o "ServerAliveInterval 45" -o "ServerAliveCountMax 2" -o "StrictHostKeyChecking no" \
    -i /home/autossh/.ssh/id_rsa \
    -p $SSH_TUNNEL_PORT \
    $SSH_TUNNEL_MODE \
    $SSH_TUNNEL_LOCALPORT:$SSH_TUNNEL_IP:$SSH_TUNNEL_REMOTEPORT \
    $SSH_TUNNEL_USER@$SSH_TUNNEL_HOST
