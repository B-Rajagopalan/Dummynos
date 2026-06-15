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

  orderStatuses: { [orderId: number]: 'pending' | 'completed' } = {};

  constructor(private pizzaService: PizzaService) {}

  ngOnInit(): void {
    this.loadStatusesFromStorage();
    this.fetchAllOrders();
  }

  loadStatusesFromStorage(): void {
    const stored = localStorage.getItem('dummynos_order_statuses');
    if (stored) {
      try {
        this.orderStatuses = JSON.parse(stored);
      } catch (e) {
        this.orderStatuses = {};
      }
    }
  }

  saveStatusesToStorage(): void {
    localStorage.setItem('dummynos_order_statuses', JSON.stringify(this.orderStatuses));
  }

  getOrderStatus(orderId?: number): 'pending' | 'completed' {
    if (!orderId) return 'pending';
    return this.orderStatuses[orderId] || 'pending';
  }

  toggleOrderStatus(orderId?: number): void {
    if (!orderId) return;
    const current = this.getOrderStatus(orderId);
    this.orderStatuses[orderId] = current === 'pending' ? 'completed' : 'pending';
    this.saveStatusesToStorage();
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
