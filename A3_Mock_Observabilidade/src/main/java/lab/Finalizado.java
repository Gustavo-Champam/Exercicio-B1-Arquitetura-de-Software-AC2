package lab;
import jakarta.persistence.*;
@Entity public class Finalizado {
 @Id private Long id;
 private String vencedor;
 protected Finalizado() {}
 public Finalizado(long id,String vencedor) { this.id=id; this.vencedor=vencedor; }
 public Long getId() { return id; }
 public String getVencedor() { return vencedor; }
}
