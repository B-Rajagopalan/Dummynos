import { Component, OnInit } from '@angular/core';
import { PizzaService } from '../../services/pizza.service';
import { PizzaOrder } from '../../models/pizza-order.model';

@Component({
  selector: 'app-order-history',
  templateUrl: './order-history.component.html',
  styleUrls: ['./order-history.component.css']
})
export class OrderHistoryComponent implements OnInit {
  orders: PizzaOrder[] = [];
  isLoading = false;
  errorMessage = '';

  searchType = 'all'; // 'all', 'pizzaName', 'contactNumber'
  searchValue = '';

  constructor(private pizzaService: PizzaService) {}

  ngOnInit(): void {
    this.fetchAllOrders();
  }

  fetchAllOrders(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.pizzaService.getPizzaDetails().subscribe({
      next: (data) => {
        this.orders = data || [];
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Failed to load order history.';
        this.isLoading = false;
      }
    });
  }

  onSearch(): void {
    if (this.searchType === 'all' || !this.searchValue.trim()) {
      this.fetchAllOrders();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    if (this.searchType === 'pizzaName') {
      this.pizzaService.getDetailsByPizzaName(this.searchValue.trim()).subscribe({
        next: (data) => {
          this.orders = data || [];
          this.isLoading = false;
        },
        error: (err) => {
          this.errorMessage = 'Failed to load orders or no orders found.';
          this.orders = [];
          this.isLoading = false;
        }
      });
    } else if (this.searchType === 'contactNumber') {
      this.pizzaService.getDetailsByContactNumber(this.searchValue.trim()).subscribe({
        next: (data) => {
          this.orders = data || [];
          this.isLoading = false;
        },
        error: (err) => {
          this.errorMessage = 'Failed to load orders or no orders found.';
          this.orders = [];
          this.isLoading = false;
        }
      });
    }
  }

  resetSearch(): void {
    this.searchType = 'all';
    this.searchValue = '';
    this.fetchAllOrders();
  }
}
