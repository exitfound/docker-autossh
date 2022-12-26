FROM ubuntu:20.04 AS autossh

ARG SSH_PRV_KEY
ENV SSH_PRV_KEY=${SSH_PRV_KEY}

WORKDIR /home/autossh/

COPY autossh.sh .

RUN apt-get update \
    && apt-get install -y \
    autossh \
    net-tools \
    iproute2 \
    && useradd -g users -d /home/autossh autossh \
    && mkdir -p /home/autossh/.ssh/ \
    && echo "$SSH_PRV_KEY" > /home/autossh/.ssh/id_rsa \
    && chmod -R 700 /home/autossh/.ssh \
    && chmod 600 /home/autossh/.ssh/id_rsa \
    && chmod +x /home/autossh/autossh.sh \
    && chown -R autossh:users /home/autossh

USER autossh

ENTRYPOINT ./autossh.sh
