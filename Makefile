all: up

COMPOSE_FILE=./docker-compose.yaml

build:
	docker compose -f $(COMPOSE_FILE) build

up:
	docker compose -f $(COMPOSE_FILE) up -d

down:
	docker compose -f $(COMPOSE_FILE) down

clean: down
	docker compose -f $(COMPOSE_FILE) down --volumes --remove-orphans

fclean: clean
	docker compose -f $(COMPOSE_FILE) down --volumes --remove-orphans --rmi all

re: fclean all

.PHONY: all volumes build up down clean fclean re
