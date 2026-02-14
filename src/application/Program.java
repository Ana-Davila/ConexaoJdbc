package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import db.DB;

public class Program {

	public static void main(String[] args) {
		// Define o formato de data brasileiro para facilitar o input
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
		Connection conn = null;
		PreparedStatement st = null; // Objeto para montar o comando SQL com segurança
		
		try {
			// 1. Conecta ao banco de dados usando a sua classe utilitária
			conn =  DB.getConnection();
			
			// 2. Prepara o comando SQL. Os "?" evitam ataques de SQL Injection.
			// O parâmetro RETURN_GENERATED_KEYS serve para recuperar o ID que o banco vai criar.
			st = conn.prepareStatement(
					"INSERT INTO seller " + 
					"(Name, Email, BirthDate, BaseSalary, DepartmentId)"+
					"VALUES"+
					"(?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS
					);
			
			// 3. Preenche os campos (placeholders) de acordo com a ordem dos "?" no SQL
			st.setString(1, "Carl Purple");
			st.setString(2, "carl@gmail.com");
			// Converte a data do Java (util) para a data do Banco de Dados (sql)
			st.setDate(3, new java.sql.Date(sdf.parse("22/04/1995").getTime()));
			st.setDouble(4, 3000.0); // Nota: 3.000 é interpretado como 3.0 no Java
			st.setInt(5, 4);
			
			// 4. Executa o comando e retorna quantas linhas foram alteradas
			int rowsAffected = st.executeUpdate();
			
			// 5. Se inseriu com sucesso, recupera o ID gerado automaticamente
			if(rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys(); // O ID vem dentro de um "objeto tabela" (ResultSet)
				while (rs.next()) {
					int id = rs.getInt(1); // Pega o valor da primeira coluna (o ID)
					System.out.println("Done! Id = "+ id);
				}
			}
			else {
				System.out.println("No rows affected");
			}
		}
		catch(SQLException e) {
			// Erros relacionados ao banco de dados (conexão, sintaxe SQL)
			e.printStackTrace();
		}
		catch (ParseException e) {
			// Erro se a data informada no parse estiver em formato inválido
			e.printStackTrace();
		}
		finally {
			// 6. SEMPRE fecha os recursos para evitar vazamento de memória (Memory Leak)
			DB.closeStatement(st);
			DB.closeConnection();
		}
	}
}