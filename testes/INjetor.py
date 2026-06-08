import requests
import json
import random
from datetime import datetime

# ==========================================
# 1. GERADORES DE DADOS
# ==========================================

def gerar_cpf_valido():
    cpf = [random.randint(0, 9) for _ in range(9)]
    # Calcula o primeiro dígito
    soma1 = sum((10 - i) * num for i, num in enumerate(cpf))
    d1 = 0 if (soma1 % 11) < 2 else 11 - (soma1 % 11)
    cpf.append(d1)
    # Calcula o segundo dígito
    soma2 = sum((11 - i) * num for i, num in enumerate(cpf))
    d2 = 0 if (soma2 % 11) < 2 else 11 - (soma2 % 11)
    cpf.append(d2)
    return ''.join(map(str, cpf))

def gerar_contas_validas(quantidade):
    contas = []
    for i in range(quantidade):
        contas.append({
            "titular": f"Cliente Válido {i+1}",
            "cpf": gerar_cpf_valido(),
            "chavePix": f"cliente{i+1}@devbank.com", # Apenas o necessário
            "celular": f"849{random.randint(10000000, 99999999)}",
            "email": f"cliente{i+1}@devbank.com"
        })
    return contas

def gerar_contas_invalidas():
    # Aqui nós atacamos cirurgicamente as regras que você criou nas constraints do Grails
    return [
        {"titular": "", "cpf": gerar_cpf_valido(), "chavePix": "pix1", "celular": "84999999999", "email": "teste@email.com"}, # Titular em branco
        {"titular": "Invalido 2", "cpf": "11111111111", "chavePix": "pix2", "celular": "84999999999", "email": "teste@email.com"}, # CPF falso
        {"titular": "Invalido 3", "cpf": gerar_cpf_valido(), "chavePix": "chave com espaco", "celular": "84999999999", "email": "teste@email.com"}, # Pix com espaço
        {"titular": "Invalido 4", "cpf": gerar_cpf_valido(), "chavePix": "pix4", "celular": "84999", "email": "teste@email.com"}, # Celular tamanho errado
        {"titular": "Invalido 5", "cpf": gerar_cpf_valido(), "chavePix": "pix5", "celular": "84999999999", "email": "teste@.com"}, # Email sem formato
        # Multiplicando os erros base para bater 20 contas com falhas diversas
        *[{
            "titular": f"Invalido Aleatorio {i}",
            "cpf": "12345678900", # CPF matemático inválido
            "chavePix": f"pix{i}",
            "celular": "0000",
            "email": "email_ruim"
        } for i in range(6, 21)]
    ]

# ==========================================
# 2. MOTOR DE REQUISIÇÕES E LOG NO LINUX
# ==========================================

URL_API = "http://localhost:8081/api/contaCorrente"
ARQUIVO_LOG = "relatorio_devbank.txt"

def atirar_contra_api():
    contas_validas = gerar_contas_validas(20)
    contas_invalidas = gerar_contas_invalidas()

    todas_as_contas = contas_validas + contas_invalidas

    with open(ARQUIVO_LOG, "w", encoding="utf-8") as arquivo:
        arquivo.write(f"=== RELATÓRIO DE TESTES DE CARGA DEVBANK ===\n")
        arquivo.write(f"Data da execução: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n\n")

        for index, conta in enumerate(todas_as_contas):
            tipo = "VÁLIDA" if index < 20 else "INVÁLIDA"

            try:
                # Dispara o JSON para o Grails
                response = requests.post(URL_API, json=conta)

                linha_log = (
                    f"[{tipo}] Titular: {conta.get('titular', 'Vazio')} | "
                    f"Status HTTP: {response.status_code} | "
                    f"Resposta: {response.text}\n"
                    f"-" * 80 + "\n"
                )

                # Imprime no terminal e salva no arquivo txt
                print(linha_log.strip())
                arquivo.write(linha_log)

            except requests.exceptions.ConnectionError:
                erro_msg = "ERRO FATAL: O servidor do Grails está desligado ou a porta 8080 está inacessível.\n"
                print(erro_msg)
                arquivo.write(erro_msg)
                break

if __name__ == "__main__":
    print("Iniciando bateria de testes contra a API do DevBank...")
    atirar_contra_api()
    print(f"\nTestes concluídos! Verifique o arquivo '{ARQUIVO_LOG}' para o relatório completo.")
