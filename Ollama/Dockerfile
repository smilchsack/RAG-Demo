FROM ollama/ollama as builder

WORKDIR /root

COPY pull.sh /root/pull.sh

RUN chmod +x /root/pull.sh

RUN /root/pull.sh

FROM ollama/ollama

WORKDIR /root

ENV PORT=11434
EXPOSE $PORT

COPY start.sh /root/start.sh
COPY --from=builder /root/.ollama /root/.ollama

RUN chmod +x /root/start.sh

RUN /root/start.sh