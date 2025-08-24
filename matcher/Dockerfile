FROM python:3.11-slim

ENV PIP_NO_CACHE_DIR=1 PYTHONDONTWRITEBYTECODE=1 PYTHONUNBUFFERED=1
WORKDIR /app

# 1) OS 런타임(특히 libgomp1)
RUN apt-get update && apt-get install -y --no-install-recommends \
    libgomp1 libstdc++6 && rm -rf /var/lib/apt/lists/*

# 2) torch는 CPU wheel로 명시 설치
COPY requirements.txt .
RUN pip install --no-cache-dir --index-url https://download.pytorch.org/whl/cpu \
    torch==2.4.1 torchvision==0.19.1 torchaudio==2.4.1

# 3) 나머지 의존성
RUN pip install --no-cache-dir -r requirements.txt

# 4) 앱 복사 및 실행
COPY . .
CMD ["uvicorn","app:app","--host","0.0.0.0","--port","8001","--workers","1"]
