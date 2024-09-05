# ArchPlugin

Этот плагин предназначен для автоматизации создания архитектурных компонентов в Android Studio.

## Установка

1. Клонируйте репозиторий:
   ```bash
   git clone https://github.com/i-redbyte/ArchPlugin.git
   cd ArchPlugin
   ```

2. Соберите проект:
   ```bash
   ./gradlew build
   ```

3. Установите плагин в Android Studio:
    - Откройте `File > Settings > Plugins > Install plugin from disk`.
    - Укажите путь до `.jar` файла из папки `build/libs`.
    - ![install_dialog](/misc/install.png)

4. **Альтернативный вариант**:
    - Вы можете скачать уже собранный `.jar` файл с [релизов проекта](https://github.com/i-redbyte/ArchPlugin/releases).

## Использование

### Создание новой фичи

1. Перейдите в меню **File > New > Feature** и выберите опцию для создания новой фичи.

![install_dialog](/misc/create_feature_file_menu.png)

Или правой кнопкой мыши в окне  **project** "New > Feature"

![install_dialog](/misc/create_feature.png)

2. В открывшемся диалоговом окне доступны следующие опции:

![install_dialog](/misc/dialog.png)

- **Select Directory**: выберите директорию для размещения новой фичи (по умолчанию feature).
- **Feature Name**: укажите имя фичи прописными буквами, разделяя слова через **-**.
- **Use custom package name**: установите пользовательский пакет, если это необходимо.
- **With State**: добавляет управление состоянием.
- **With Actions**: добавляет действия для изменения состояния.
- **With Effect**: добавляет побочные эффекты.
- **Create DI Components**: автоматически создает компоненты для внедрения зависимостей.

![Create Feature Dialog](/misc/dialog_2.png)

### Шаги создания фичи:

1. В поле **Select directory** укажите директорию, в которой будет создана новая фича.
2. Введите название фичи в поле **Feature name**.
3. Если необходимо, используйте собственное имя пакета, установив галочку **Use custom package name**.
4. Настройте требуемые элементы:
    - **With State**: если хотите, чтобы ваша фича включала управление состоянием.
    - **With Actions**: если хотите включить действия.
    - **With Effect**: если требуется добавить побочные эффекты.
5. Установите опцию **Create DI Components**, если хотите автоматически сгенерировать компоненты для внедрения
   зависимостей.

### Примерный состав нового модуля:

![Create Feature Dialog](/misc/struct.png)

