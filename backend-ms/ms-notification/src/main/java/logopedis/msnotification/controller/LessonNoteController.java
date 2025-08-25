package logopedis.msnotification.controller;

import logopedis.libentities.msnotification.entity.LessonNote;
import logopedis.msnotification.service.LessonNoteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lesson-note")
public class LessonNoteController {
    private final LessonNoteService service;
    public LessonNoteController(LessonNoteService service) {
        this.service = service;
    }

    @PostMapping("/findall")
    public List<LessonNote> findall()  {
        var result = service.findall();
        return result;
    }

    @PostMapping("/find-by-id")
    public LessonNote findById(@RequestBody Long id)  {
        var result = service.findById(id);
        return result;
    }

    @PostMapping("/create")
    public LessonNote create(@RequestBody LessonNote note)  {
        var result = service.create(note);
        return result;
    }

    @PutMapping("/update")
    public LessonNote update(@RequestBody LessonNote note)  {
        var result = service.update(note);
        return result;
    }
    @DeleteMapping("/delete")
    public LessonNote delete(@RequestBody Long id)  {
        var result = service.delete(id);
        return result;
    }
}
