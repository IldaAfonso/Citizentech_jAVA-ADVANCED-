# Rode dentro da pasta citizentech-java. Antes: crie o repo PÚBLICO "citizentech-java" no GitHub (vazio).
git init
git branch -M main
git add pom.xml .gitignore src/main/resources
git commit -m "chore: projeto Spring Boot, dependencias e configuracao"
git add src/main/java/br/fiap/citizentech/model src/main/java/br/fiap/citizentech/CitizenTechApplication.java
git commit -m "feat: entidades Cuidador, Idoso, Evento e Usuario"
git add src/main/java/br/fiap/citizentech/dto src/main/java/br/fiap/citizentech/repository
git commit -m "feat: DTOs e repositorios (query method, JPQL e Specification)"
git add src/main/java/br/fiap/citizentech/service src/main/java/br/fiap/citizentech/exception
git commit -m "feat: servicos com cache e tratamento de erros"
git add src/main/java/br/fiap/citizentech/security
git commit -m "feat: seguranca com JWT e papeis ADMIN/CUIDADOR"
git add src/main/java/br/fiap/citizentech/controller src/main/java/br/fiap/citizentech/config
git commit -m "feat: controllers REST com HATEOAS, paginacao e Swagger"
git add README.md PITCH.md ENTREGA_exemplo.txt
git commit -m "docs: README e roteiro do pitch"
git remote add origin https://github.com/IldaAfonso/citizentech-java.git
git push -u origin main
