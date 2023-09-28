import { html, LitElement} from "lit";

class InfoCardRow extends LitElement {
    static get properties() {
        return {
            label: { type: String }
        }
    }

    render() {
        return html`
            <div class="p-s bg-contrast-5">
                <span class="font-medium">${this.label}</span>
            </div>
            <div class="p-s pr-m w-full">
                <slot></slot>
            </div>
        `;
    }
}

window.customElements.define('info-card-row', InfoCardRow);