FROM opensuse/leap:15.3 AS autossh

ARG SSH_PRV_KEY
ENV SSH_PRV_KEY=${SSH_PRV_KEY}
RUN zypper --non-interactive --no-gpg-checks install -y --force \
    vim \
    curl \
    autossh \
    net-tools \
    iputils \
    iproute2 \
    && useradd -g users -d /home/autossh autossh \
    && mkdir -p /home/autossh/.ssh/ \
    && chmod -R 700 /home/autossh/.ssh \
    && echo "$SSH_PRV_KEY" > /home/autossh/.ssh/id_rsa \
    && chmod 600 /home/autossh/.ssh/id_rsa \ 
    && chown -R autossh:users /home/autossh

WORKDIR /home/autossh/
USER autossh

ENTRYPOINT autossh -N -M 0 -o "ServerAliveInterval 45" -o "ServerAliveCountMax 2" -o "StrictHostKeyChecking no" -i /home/autossh/.ssh/id_rsa -p $SSH_PORT -L $SSH_TUNNEL $SSH_USER@$SSH_HOST
