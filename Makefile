all: run1 run2

run1: ## Export ssh key
	export SSH_PRV_KEY="$(cat ~/.ssh/id_rsa)"

run2: ## Run docker-compose
	docker-compose -f autossh-docker-compose.yaml up -d
