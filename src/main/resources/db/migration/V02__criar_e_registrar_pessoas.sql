CREATE TABLE pessoa (
	codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
	nome VARCHAR(50) NOT NULL,
	logradouro VARCHAR(100),
	numero VARCHAR(10),
	complemento VARCHAR(100),
	bairro VARCHAR(30),
	cep VARCHAR(10),
	cidade VARCHAR(50),
	estado VARCHAR(20),
	ativo BIT(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO pessoa (nome, logradouro, numero, complemento, bairro, cep, cidade, estado, ativo) VALUES ('Pedro', 'Rua Um', '1111', 'Próx. à academia', 'Conjunto A', '64000-000', 'Teresina', 'PI', 1);
INSERT INTO pessoa (nome, logradouro, numero, complemento, bairro, cep, cidade, estado, ativo) VALUES ('Luis', 'Rua Dois', '2222', NULL, 'Conjunto B', '64000-000', 'Teresina', 'PI', 1);
INSERT INTO pessoa (nome, logradouro, numero, complemento, bairro, cep, cidade, estado, ativo) VALUES ('Alexandre', 'Rua Dois', '2222', NULL, 'Conjunto B', '64000-000', 'Teresina', 'PI', 1);
INSERT INTO pessoa (nome, logradouro, numero, complemento, bairro, cep, cidade, estado, ativo) VALUES ('Alex', 'Rua Um', '1111', 'Próx. à academia', 'Conjunto A', '64000-000', 'Teresina', 'PI', 1);