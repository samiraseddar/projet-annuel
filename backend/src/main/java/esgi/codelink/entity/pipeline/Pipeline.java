package esgi.codelink.entity.pipeline;

import esgi.codelink.entity.User;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pipelines")
public class Pipeline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "pipeline", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PipelineJob> jobs = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PipelineStatus status = PipelineStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    public enum PipelineStatus {
        PENDING,
        RUNNING,
        COMPLETED,
        FAILED
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<PipelineJob> getJobs() {
        return jobs;
    }

    public PipelineStatus getStatus() {
        return status;
    }

    public void setStatus(PipelineStatus status) {
        this.status = status;
    }

    public void setJobs(List<PipelineJob> jobs) {
        this.jobs = jobs;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Pipeline{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", jobs=" + jobs +
                ", status=" + status +
                '}';
    }
}
