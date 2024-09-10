import { useSignal } from "@vaadin/hilla-react-signals";
import { Checkbox, HorizontalLayout, VerticalLayout } from "@vaadin/react-components";
import { TextField } from "@vaadin/react-components/TextField.js";
import { Button } from "@vaadin/react-components/Button.js";
import { ViewConfig } from "@vaadin/hilla-file-router/types.js";

export const config: ViewConfig = { // configuration parameters (view title and icon)
    menu: { order: 1, icon: 'line-awesome/svg/tasks-solid.svg' },
    title: "Vaadin React Todo"
}

export default function TodoView() {
    const todos = useSignal<string[]>([]) // list of tasks (string[])
    const task = useSignal<string>("") // description of the next task to add

    function addTask() { // function that add the task to the list of tasks
        todos.value = [...todos.value, task.value] // append
        task.value = "" // delete description
    }
	
	/* 
		Each item in the ToDo list is rendered as a checkbox (ckeckbox and label in a VerticalLayout).
		Description (TextField) and add button (button with the onClick function specified above) are in a VerticalLayout
	*/
	
    return <VerticalLayout theme="spacing padding">
		        <VerticalLayout>
		            {todos.value.map((todo,index) => <Checkbox key={index} label={todo}/>)}
		        </VerticalLayout>
		        <HorizontalLayout theme="spacing">
		            <TextField value={task.value} onChange={e => task.value = e.target.value}/> 
		            <Button onClick={addTask}>Add</Button> 
		        </HorizontalLayout>
		    </VerticalLayout>
}