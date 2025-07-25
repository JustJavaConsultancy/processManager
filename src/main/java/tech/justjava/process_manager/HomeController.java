package tech.justjava.process_manager;

import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tech.justjava.process_manager.support.SupportFeignClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;
    private final SupportFeignClient supportFeignClient;

    @Value("${app.processKey}")
    private String processKey;

    public HomeController(
            RuntimeService runtimeService,
            TaskService taskService,
            HistoryService historyService,
            SupportFeignClient supportFeignClient
    ) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.historyService = historyService;
        this.supportFeignClient = supportFeignClient;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long processInstancesCount = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey(processKey)
                .active()
                .count();

        List<Task> activeTasks = taskService.createTaskQuery()
                .processDefinitionKey(processKey)
                .active()
                .list();

        long activeTasksCount = activeTasks.size();

        long completedTasksCount = historyService.createHistoricTaskInstanceQuery()
                .processDefinitionKey(processKey)
                .finished()
                .orderByHistoricTaskInstanceEndTime()
                .desc()
                .count();

        model.addAttribute("processInstancesCount", processInstancesCount);
        model.addAttribute("activeTasksCount", activeTasksCount);
        model.addAttribute("completedTasksCount", completedTasksCount);
        model.addAttribute("activeTasks", activeTasks);

        return "dashboard";
    }

    // === SUPPORT CHAT BACKEND ===
    @PostMapping("/api/chat/send")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleChatMessage(@RequestParam("message") String message) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Prepare the payload for the AI backend
            Map<String, Object> aiPayload = new HashMap<>();
            aiPayload.put("userPrompt", message);

            // Send it using Feign client
            ResponseEntity<String> aiResponse = supportFeignClient.postAiMessage(aiPayload);

            response.put("status", "success");
            response.put("response", aiResponse.getBody());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // log for debugging
            response.put("status", "error");
            response.put("response", "Sorry, something went wrong.");
            return ResponseEntity.status(500).body(response);
        }
    }
}
