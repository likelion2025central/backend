FROM python:3.11-slim

ENV PIP_NO_CACHE_DIR=1 PYTHONDONTWRITEBYTECODE=1 PYTHONUNBUFFERED=1
WORKDIR /app

# 런타임 라이브러리
RUN apt-get update && apt-get install -y --no-install-recommends \
    libgomp1 libstdc++6 && rm -rf /var/lib/apt/lists/*

RUN pip install --no-cache-dir --upgrade pip

# 1) torch 2.6.0 고정 (CPU wheel)
RUN pip install --no-cache-dir --index-url https://download.pytorch.org/whl/cpu \
    torch==2.6.0 torchvision==0.21.0 torchaudio==2.6.0

# 2) Huggingface 관련 주요 deps는 수동으로 먼저 설치
RUN pip install --no-cache-dir transformers safetensors sentencepiece accelerate scikit-learn pandas datasets

# 3) FlagEmbedding은 deps 무시하고 설치 (torch 건드리지 않도록)
RUN pip install --no-cache-dir --no-deps "FlagEmbedding>=1.2.10"

# 4) fastapi/uvicorn 등 나머지
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

CMD ["uvicorn","app:app","--host","0.0.0.0","--port","8001","--workers","1"]
