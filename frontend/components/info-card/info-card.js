import {html, LitElement} from "lit";
import {customElement} from "lit/decorators.js";

@customElement("info-card")
class InfoCard extends LitElement {


    render() {
        return html`
            <div class="grid grid-cols-2 border border-contrast-10 ">
                <slot></slot>
            </div>
        `;
    }
}

