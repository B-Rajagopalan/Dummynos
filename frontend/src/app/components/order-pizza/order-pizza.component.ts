import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PizzaService } from '../../services/pizza.service';
import { PizzaOrder } from '../../models/pizza-order.model';

@Component({
  selector: 'app-order-pizza',
  templateUrl: './order-pizza.component.html',
  styleUrls: ['./order-pizza.component.css']
})
export class OrderPizzaComponent implements OnInit {
  orderForm!: FormGroup;
  isSubmitting = false;
  successMessage = '';
  errorMessage = '';

  famousPizzas = [
    'Margherita',
    'Pepperoni',
    'BBQ Chicken',
    'Hawaiian',
    'Veggie Supreme',
    'Meat Lovers',
    'Four Cheese',
    'Mushroom Truffle'
  ];

  constructor(private fb: FormBuilder, private pizzaService: PizzaService) {}

  ngOnInit(): void {
    this.orderForm = this.fb.group({
      pizzaName: ['', Validators.required],
      quantity: [1, [Validators.required, Validators.min(1)]],
      bill: ['', Validators.required],
      customerContactNumber: ['', [
        Validators.required, 
        Validators.pattern('^[0-9]{10}$'), // Exactly 10 digits
        Validators.minLength(10), 
        Validators.maxLength(10)
      ]]
    });
  }

  // Calculate a mock bill automatically based on quantity, or let user input. 
  // Let's auto-calculate but allow them to overwrite it.
  updateBill() {
    const qty = this.orderForm.get('quantity')?.value;
    if (qty && qty >= 1) {
      this.orderForm.patchValue({ bill: qty * 15.99 }); // $15.99 per pizza default
    }
  }

  onSubmit(): void {
    if (this.orderForm.invalid) {
      this.orderForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.successMessage = '';
    this.errorMessage = '';

    const orderData: PizzaOrder = this.orderForm.value;

    this.pizzaService.addPizza(orderData).subscribe({
      next: (response) => {
        this.successMessage = response || 'Pizza ordered successfully!';
        this.isSubmitting = false;
        this.orderForm.reset({ quantity: 1 });
      },
      error: (err) => {
        this.errorMessage = err.error || 'Failed to place order. Please try again.';
        this.isSubmitting = false;
      }
    });
  }
}
