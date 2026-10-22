CREATE TABLE offers (
  id BIGINT PRIMARY KEY,
  title VARCHAR(120) NOT NULL,
  merchant VARCHAR(120) NOT NULL,
  district VARCHAR(80) NOT NULL,
  category VARCHAR(40) NOT NULL,
  description TEXT NOT NULL,
  pickup VARCHAR(40) NOT NULL,
  price NUMERIC(10,2) NOT NULL CHECK (price >= 0),
  original_price NUMERIC(10,2) NOT NULL CHECK (original_price >= price),
  image VARCHAR(255) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO offers (id,title,merchant,district,category,description,pickup,price,original_price,image) VALUES
(1,'Cesta da fornada','Pão de Lume','Bessa','Padaria','Uma seleção surpresa de pães feitos hoje. O conteúdo varia conforme a fornada do dia.','17h30 – 19h',19.90,39.90,'/images/bread.jpg'),
(2,'Almoço do quintal','Quintal Cozinha','Manaíra','Refeições','Uma refeição preparada no dia, com vegetais e acompanhamentos disponíveis na cozinha.','15h – 17h',24.90,48.90,'/images/bowl.jpg'),
(3,'Surpresa da vitrine','Dona Miga','Tambaú','Doces','Doces e massas folhadas da vitrine de hoje, escolhidos pela equipe da casa.','18h – 19h30',16.90,34.90,'/images/pastry.jpg'),
(4,'Pães para partilhar','Casa do Trigo','Cabo Branco','Padaria','Pães e itens de padaria feitos no dia para levar para casa e dividir.','18h – 20h',22.90,45.90,'/images/bakery.jpg');
