import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PizzaOrder } from '../models/pizza-order.model';

@Injectable({
  providedIn: 'root'
})
export class PizzaService {

  private baseUrl = '/pizza/controller';

  constructor(private http: HttpClient) { }

  addPizza(order: PizzaOrder): Observable<string> {
    return this.http.put(`${this.baseUrl}/addPizza`, order, { responseType: 'text' });
  }

  getPizzaDetails(): Observable<PizzaOrder[]> {
    return this.http.get<PizzaOrder[]>(`${this.baseUrl}/getPizzaDetails`);
  }

  getDetailsByPizzaName(pizzaName: string): Observable<PizzaOrder[]> {
    return this.http.post<PizzaOrder[]>(`${this.baseUrl}/getDetailsByPizzaName/${pizzaName}`, {});
  }

  getDetailsByContactNumber(contactNumber: string): Observable<PizzaOrder[]> {
    return this.http.post<PizzaOrder[]>(`${this.baseUrl}/getDetailsByContactNumber/${contactNumber}`, {});
  }
}
