# Makefile for TeekaSarthi Spring Boot Backend

start-local:
	@echo "🚀 Starting TeekaSarthi local environment..."
	docker-compose -f docker-compose.dev.yml up --build

stop-local:
	@echo "🛑 Stopping TeekaSarthi containers..."
	docker-compose -f docker-compose.dev.yml down

logs:
	@echo "📜 Streaming logs from backend..."
	docker-compose -f docker-compose.dev.yml logs -f backend

rebuild:
	@echo "♻️ Rebuilding backend image..."
	docker-compose -f docker-compose.dev.yml build --no-cache

clean:
	@echo "🧹 Removing all Docker data..."
	docker system prune -f
