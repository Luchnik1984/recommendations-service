
set -e

echo " Настройка окружения для Recommendation Service"

# Проверяем существует ли .env
if [ -f configuration.env ]; then
    echo "️ Файл .env уже существует. Хотите перезаписать? (y/N)"
    read -r response
    if [[ ! "$response" =~ ^([yY][eE][sS]|[yY])+$ ]]; then
        echo "!!! Отменено."
        exit 0
    fi
fi

# Копируем шаблон
cp .env.example configuration.env

echo " Файл configuration.env создан из шаблона .env.example"

# Создаем configuration.env.dev если не существует
 if [ ! -f configuration.env.dev ]; then
    cp .env.example configuration.env.dev
    echo " Файл configuration.env.dev создан из шаблона"

fi


echo ""
 echo " Пожалуйста, отредактируйте файлы конфигурации:"
 echo "   - configuration.env - общие настройки"
 echo "   - configuration.env.dev - настройки для разработки"

 echo ""
 echo " Обязательные настройки:"
 echo "   - POSTGRES_URL, POSTGRES_USERNAME, POSTGRES_PASSWORD"
 echo "   - H2_DATABASE_URL (если нужно)"
 echo "   - SERVER_PORT (если нужно)"
 echo ""
 echo " Для редактирования:"
 echo "   nano configuration.env"
 echo "   nano configuration.env.dev"
 echo ""
 echo " Для запуска приложения: ./mvnw spring-boot:run"