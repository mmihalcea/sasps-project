import { Component } from '@angular/core';
import {Button} from "primeng/button";
import {FloatLabel} from "primeng/floatlabel";
import {FormsModule} from "@angular/forms";
import {InputGroup} from "primeng/inputgroup";
import {InputText} from "primeng/inputtext";
import {RouterLink} from "@angular/router";
import {InputGroupAddon} from 'primeng/inputgroupaddon';

@Component({
  selector: 'app-home',
  imports: [
    Button,
    FloatLabel,
    FormsModule,
    InputGroup,
    InputText,
    RouterLink,
    InputGroupAddon
  ],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  protected email: any;
}
