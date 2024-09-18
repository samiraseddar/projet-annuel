package esgi.codelink.dto;

import esgi.codelink.entity.Comment;

public class CommentDTO {

    private Long scriptId;
    private String content;


    public CommentDTO() {
    }

    public CommentDTO(Long scriptId, String content) {
        this.scriptId = scriptId;
        this.content = content;
    }

    public Long getScriptId() {
        return scriptId;
    }

    public void setScriptId(Long scriptId) {
        this.scriptId = scriptId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Comment convertToComment() {
        Comment comment = new Comment();
        comment.setContent(this.content);
        return comment;
    }

    @Override
    public String toString() {
        return "CommentDTO{" +
                "scriptId=" + scriptId +
                ", content='" + content + '\'' +
                '}';
    }
}