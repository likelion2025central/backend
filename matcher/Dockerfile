FROM python:3.11-slim

ENV PIP_NO_CACHE_DIR=1 PYTHONDONTWRITEBYTECODE=1 PYTHONUNBUFFERED=1
WORKDIR /app

# 런타임 라이브러리
RUN apt-get update && apt-get install -y --no-install-recommends \
    libgomp1 libstdc++6 && rm -rf /var/lib/apt/lists/*

# (선택) pip 업그레이드
RUN pip install --no-cache-dir --upgrade pip

# torch는 **CPU 전용 인덱스**에서 2.6+로 먼저 설치
# torchvision/torchaudio는 torch 2.6 라인과 맞춰주세요 (예: 2.6.x)
RUN pip install --no-cache-dir --index-url https://download.pytorch.org/whl/cpu \
    torch==2.6.0 torchvision==0.21.0 torchaudio==2.6.0

# 나머지 패키지 설치
COPY requirements.txt .
# requirements.txt 에서 torch 항목은 제거(중복/다운그레이드 방지)
RUN pip install --no-cache-dir -r requirements.txt

# 앱 복사
COPY . .

# 워커 1개로 먼저 안정 기동
CMD ["uvicorn","app:app","--host","0.0.0.0","--port","8001","--workers","1"]
