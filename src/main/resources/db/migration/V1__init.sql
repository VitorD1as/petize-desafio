-- Schema alinhado às entidades JPA (nomes de colunas físicos do Hibernate com naming snake_case,
-- exceto @JoinColumn com nome explícito).

CREATE TABLE `TB_USER` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `nome` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `senha` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE UNIQUE INDEX `UK_tb_user_email` ON `TB_USER` (`email`);

CREATE TABLE `TB_TASK` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `titulo` VARCHAR(255) NOT NULL,
  `descricao` VARCHAR(255) NOT NULL,
  `data_vencimento` DATE DEFAULT NULL,
  `status` VARCHAR(255) DEFAULT NULL,
  `prioridade` VARCHAR(255) DEFAULT NULL,
  `idTarefaPrincipal` BIGINT DEFAULT NULL,
  `idUsuario` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_tb_task_user` FOREIGN KEY (`idUsuario`) REFERENCES `TB_USER` (`id`),
  CONSTRAINT `FK_tb_task_principal` FOREIGN KEY (`idTarefaPrincipal`) REFERENCES `TB_TASK` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
