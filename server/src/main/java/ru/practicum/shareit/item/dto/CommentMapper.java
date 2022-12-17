package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;

@Component
public class CommentMapper {
    public Comment toComment(IncomingCommentDto incomingCommentDto) {
        return new Comment(incomingCommentDto.getId(), incomingCommentDto.getText(), null, null, null);
    }

    public IncomingCommentDto toCommentDto(Comment comment) {
        return new IncomingCommentDto(comment.getId(), comment.getText());
    }

    public OutcomingCommentDto toOutcomingCommentDto(Comment comment) {
        return new OutcomingCommentDto(comment.getId(), comment.getText(), comment.getAuthor().getName(), comment.getCreated());
    }
}
