# app.py
from fastapi import FastAPI
from pydantic import BaseModel
from FlagEmbedding import BGEM3FlagModel, FlagReranker

app = FastAPI()
emb_model = BGEM3FlagModel('BAAI/bge-m3', use_fp16=True)  # 1024-d, no query instr.
reranker = FlagReranker('BAAI/bge-reranker-v2-m3', use_fp16=True)

class EmbedReq(BaseModel):
    texts: list[str]

class RerankReq(BaseModel):
    query: str
    candidates: list[str]
    normalize: bool = True

@app.post("/embed")
def embed(req: EmbedReq):
    dense = emb_model.encode(req.texts, batch_size=32, max_length=2048)['dense_vecs']
    return {"vectors": [v.tolist() for v in dense]}

@app.post("/rerank")
def rerank(req: RerankReq):
    pairs = [[req.query, c] for c in req.candidates]
    scores = reranker.compute_score(pairs, normalize=req.normalize)
    return {"scores": [float(s) for s in (scores if isinstance(scores, list) else [scores])]}
